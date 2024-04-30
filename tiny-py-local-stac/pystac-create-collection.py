import os
import rasterio
import pystac

from shapely.geometry import Polygon, mapping
from pystac.extensions.eo import Band
from shapely.geometry import shape

from rio_stac import create_stac_item 

IMAGE_SERVER_HOST = os.getenv('IMAGE_SERVER_HOST', '127.0.0.1')


def get_bbox_and_footprint(raster):
    with rasterio.open(raster) as r:
        print(r.meta)
        print(r.profile)
        bounds = r.bounds
        bbox = [bounds.left, bounds.bottom, bounds.right, bounds.top]
        footprint = Polygon([
            [bounds.left, bounds.bottom],
            [bounds.left, bounds.top],
            [bounds.right, bounds.top],
            [bounds.right, bounds.bottom]
        ])
        
        return (bbox, mapping(footprint))

def get_files_in_folder(folder_path):
    files = os.listdir(folder_path)
    files = [file for file in files if os.path.isfile(os.path.join(folder_path, file))]
    return files

imagePaths = get_files_in_folder('stac/images')
# print(imagePaths)


items = []
# items2 = []
# for imagePath in imagePaths:
#     items2.append(get_bbox_and_footprint('http://127.0.0.1:8088/'+imagePath))

bands = [
             Band.create(name='Blue', description='Blue: 450 - 510 nm', common_name='blue'),
             Band.create(name='Green', description='Green: 510 - 580 nm', common_name='green'),
             Band.create(name='Red', description='Red: 630 - 690 nm', common_name='red'),]

for imagePath in imagePaths:
    items.append(create_stac_item('http://'+IMAGE_SERVER_HOST+':8088/'+imagePath))



unioned_footprint = shape(items[0].geometry)
collectionIntervals=[]
for i in range(len(items)):
    if(i!=0):
        unioned_footprint = unioned_footprint.union(shape(items[i].geometry))
    collectionIntervals.append(items[i].datetime)

collection_bbox = list(unioned_footprint.bounds)
spatial_extent = pystac.SpatialExtent(bboxes=[collection_bbox])

temporal_extent = pystac.TemporalExtent(intervals=collectionIntervals)

collection_extent = pystac.Extent(spatial=spatial_extent, temporal=temporal_extent)

collection = pystac.Collection(id='free-samples',
                               description='Local Free Samples',
                               extent=collection_extent,
                               license='CC-BY-SA-4.0')

print(collection)


collection.add_items(items)
catalog = pystac.Catalog(id='Local Free Samples catalog', 
                         description='This Catalog is a basic demonstration of how to include a Collection in a STAC Catalog.')
catalog.add_child(collection)

catalog.describe()
catalog.normalize_and_save(root_href='stac/stac-collection',catalog_type=pystac.CatalogType.SELF_CONTAINED)
