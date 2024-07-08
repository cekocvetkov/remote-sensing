# Full Stack Solution for Remote Sensing Object and Tree Detection

This project uses Git Large File Storage. Before running the applications, you need to set it up with `git lfs install`
and download the files with `git lfs pull`
## Start the project with docker

1. git clone git@github.com:cekocvetkov/remote-sensing.git
2. cd remote-sensing
3. git lfs install
4. git lfs pull
  5. _Please make sure you deleted old docker images and containers if you had an older version of the repo and have updated at least once since_
6. `docker-compose up`


## Build and start the project locally with docker:

`docker-compose up`

The docker-compose starts the frontend and all the needed components for the full-stack solution.
After all is successfully started one can access the frontend on `localhost:3000`
(one can add own geotiff images under tiny-py-local-stac/stac/images, before starting the docker containers, which will then be available for the whole application)


## Frontend Preview:

<img width="978" alt="Screenshot 2024-04-30 at 16 18 55" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/eff9ca08-5c15-4e92-9e61-9ae353771169">
<img width="973" alt="Screenshot 2024-04-30 at 16 19 09" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/cbb045ed-2ca8-444d-9a5d-5d87502c2774">

## Architecture design

<img width="1671" alt="Screenshot 2024-03-19 at 21 05 15" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/3934422c-f2b1-4095-8b9d-cf4342c0e0db">
<img width="1666" alt="Screenshot 2024-04-30 at 16 10 50" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/68034908-0c73-41e9-8038-98f3293d2ce7">
<img width="2012" alt="Screenshot 2024-04-30 at 16 11 01" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/a2d49d90-5177-44fa-bbf8-4c5123fb03f5">
<img width="1555" alt="Screenshot 2024-04-30 at 16 11 09" src="https://github.com/cekocvetkov/remote-sensing/assets/7689051/76331853-5cc0-465a-981d-7d3233dc4d55">
