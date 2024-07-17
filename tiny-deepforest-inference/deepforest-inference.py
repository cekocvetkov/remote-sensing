from flask import Flask, request, send_file
import cv2
import io
import numpy as np
from deepforest import main
import torch

model = main.deepforest()
model.use_release()

customModel = main.deepforest()
try:
    customModel.model.load_state_dict(torch.load("./DeepForestCustomTrained.pl"))
except Exception as e:
    print(f"Error loading the custom deepforest model (Only the default deepforest model will be available). This doesn't affect the rest of the application")

app = Flask(__name__)

def treeDetectionCustom(i):
    img = customModel.predict_image(image=i, return_plot=True)
    return img

def treeDetection(i):
    img = model.predict_image(image=i, return_plot=True)
    return img


def read_image_opencv(image_file):
    nparr = np.frombuffer(image_file, np.uint8)
    cv2.normalize(nparr, None, 255, 0, cv2.NORM_MINMAX, cv2.CV_8U)

    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    image = image[..., ::-1] 
    return image

@app.route('/deepforest/<path:model>', methods=['POST'])
def get_image(model):
    print("get free samples for "+model)

    image_file = request.data
    img = read_image_opencv(image_file)
    if(model=='Deepforest_Custom'):
        try:
            image = treeDetectionCustom(img)
        except Exception as e:
            print(f"Error on tree detection with the custom trained deepforest model. Check if model was loaded successfully")
    else:
        image = treeDetection(img)

    _, img_encoded = cv2.imencode('.tif', image)

    return send_file(io.BytesIO(img_encoded.tobytes()), mimetype='image/tiff')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8081)
