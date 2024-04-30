import { Injectable } from '@angular/core';
import { View, Feature } from 'ol';
import { Extent } from 'ol/extent';
import GeoJSON from 'ol/format/GeoJSON';
import BingMaps from 'ol/source/BingMaps.js';

import { Geometry } from 'ol/geom';
import Draw, { DrawEvent, createRegularPolygon } from 'ol/interaction/Draw';
import VectorLayer from 'ol/layer/Vector';
import { transformExtent } from 'ol/proj';
import { OSM } from 'ol/source';
import VectorSource from 'ol/source/Vector';
import { Subject } from 'rxjs';
import Map from 'ol/Map';
import TileLayer from 'ol/layer/WebGLTile.js';
import Static from 'ol/source/ImageStatic';
import ImageLayer from 'ol/layer/Image.js';
import { defaults as defaultInteractions } from 'ol/interaction/defaults';
import Fill from 'ol/style/Fill';
import Style from 'ol/style/Style';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  private drawEnd$$ = new Subject<number[]>();

  private map: Map;

  private bing = new TileLayer({
    source: new BingMaps({
      key: 'Aka7WBWlT_uiKpqkCaYfCD97redC9OP0miLCvbYige0eH41SNyBiz2KgMDPAlqUE',
      imagerySet: 'Aerial',
    }),
  });
  private vectorStyle = new Style({
    fill: new Fill({
      color: 'rgba(0, 0, 0, 0)', // Transparent fill
    }),
  });
  private osm = new TileLayer({
    source: new OSM(),
  });

  geoTiffLayer: TileLayer = new TileLayer({});

  imageLayer: ImageLayer<Static> = new ImageLayer({});

  public drawEnd$ = this.drawEnd$$.asObservable();

  constructor() {
    this.map = this.createMap('map');
  }

  public setSource(image: Blob | null, extent: number[]) {
    console.log(`Set Source ${image} ${extent}`);
    if (image === null) {
      return;
    }

    console.log(extent);
    console.log(this.map.getView().calculateExtent());
    if (extent.length === 0) {
      // Means we have BING screenshot simple source
      extent = this.map.getView().calculateExtent();
      console.log(extent[0]);
      extent[0] -= 10;
      const imageUrl = URL.createObjectURL(image);
      const source = new Static({
        url: imageUrl,
        projection: 'EPSG:3857',
        imageExtent: extent,
      });
      console.log(extent);
      console.log(source);

      this.imageLayer.setExtent(extent);
      this.imageLayer.setSource(source);
      return;
    }
    const extentEPSG3857: number[] = transformExtent(
      extent!,
      'EPSG:4326',
      'EPSG:3857'
    );
    const imageUrl = URL.createObjectURL(image);
    const source = new Static({
      url: imageUrl,
      projection: 'EPSG:3857',
      imageExtent: extentEPSG3857,
    });
    console.log(extentEPSG3857);
    console.log(source);

    this.imageLayer.setExtent(extentEPSG3857);
    this.imageLayer.setSource(source);
  }

  public changeMapSource(mapSource: string) {
    const vectorLayer = new VectorLayer({
      source: new VectorSource({ wrapX: false }),
    });
    let layers = [
      this.resolve(mapSource),
      this.geoTiffLayer,
      this.imageLayer,
      vectorLayer,
    ];

    this.map.setLayers(layers);
    const drawInteraction = this.createBBoxDrawInteraction(
      vectorLayer.getSource()!
    );
    if (mapSource === 'Aerial') {
      const interactions = defaultInteractions();
      this.map.getInteractions().clear();
      interactions.forEach((interaction) =>
        this.map.addInteraction(interaction)
      );
    } else {
      this.map.addInteraction(drawInteraction);
    }
    this.map.render();
  }

  private resolve(mapSource: string) {
    if (mapSource === 'Aerial') {
      return this.bing;
    } else {
      return this.osm;
    }
  }

  private createMap(targetElementId: string): Map {
    this.initGeoTiff3BandsLayer();

    const vectorLayer = new VectorLayer({
      source: new VectorSource({ wrapX: false }),
      style: this.vectorStyle,
    });
    const drawInteraction = this.createBBoxDrawInteraction(
      vectorLayer.getSource()!
    );
    const map = new Map({
      view: new View({
        center: [0, 0],
        zoom: 0,
        projection: 'EPSG:3857',
      }),
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
        this.geoTiffLayer,
        this.imageLayer,
        vectorLayer,
      ],
    });
    map.addInteraction(drawInteraction);
    map.getControls().clear();
    setTimeout(() => {
      map.setTarget(targetElementId);
    }, 0);

    return map;
  }

  private onDrawEnd(e: DrawEvent): void {
    const feature: Feature<Geometry> = e.feature;

    feature.setStyle(this.vectorStyle);

    var writer = new GeoJSON();
    writer.writeGeometry(feature.getGeometry()!);

    const extent: Extent = feature.getGeometry()?.getExtent()!;
    const extentEPSG4326: number[] = transformExtent(
      extent!,
      'EPSG:3857',
      'EPSG:4326'
    );

    this.drawEnd$$.next(extentEPSG4326);
  }

  private createBBoxDrawInteraction(source: VectorSource) {
    const drawInteraction = new Draw({
      source: source,
      type: 'Circle',
      geometryFunction: createRegularPolygon(4, 150),
    });
    drawInteraction.on('drawend', (e: DrawEvent) => this.onDrawEnd(e));
    return drawInteraction;
  }

  private initGeoTiff3BandsLayer() {
    this.geoTiffLayer = new TileLayer({
      style: {
        variables: { red: 1, green: 2, blue: 3 },
        color: [
          'array',
          ['band', ['var', 'red']],
          ['band', ['var', 'green']],
          ['band', ['var', 'blue']],
          1,
        ],
      },
    });
  }
}
