# Serve local images through STAC specification.

This project can serve images from the local file system through STAC specification. It uses pystac under the hood and a little help from rio_stac.
It contains four parts:

1. A python script that creates a STAC catalog from a given images folder (in this project structure it is stac/images).
2. A simple http server that serves images (this simulates the home of the images which in real life projects are usually served in the cloud or s3 buckets or other APIs)
3. Starting a local http server that serves the STAC catalog itself.
4. A REST API that serves two endpoints - /free-samples?bbox=... and /free-samples/{item_id}. The first one returns the stac items that intersect with the given bounding box. The second one returns a single stac item by its id

## Run the project locally

1. Copy some images to the stac/images folder
2. Serve the images through a local http server (run ./start-image-server.sh)
3. Run pystac-create-collection.py (Creates the stac catalog, collection, items)
4. Serve the catalog itself (run ./start-stac-catalog-server.sh)
5. Start the REST API (run rest-api-free-samples.py)
