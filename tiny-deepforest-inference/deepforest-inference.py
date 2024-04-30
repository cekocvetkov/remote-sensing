from flask import Flask, request, send_file
import cv2
import io
import numpy as np
from deepforest import main


model = main.deepforest()
model.use_release()
# modelCustom = main.deepforest()
# modelCustom.model.load_state_dict(torch.load("./jdrzej-szpygiel__deepforest__10_epochs.pl"))

app = Flask(__name__)

# def treeDetectionCustom(i):
#     img = modelCustom.predict_image(image=i, return_plot=True)
#     return img

def treeDetection(i):
    img = model.predict_image(image=i, return_plot=True)
    return img


def read_image_opencv(image_file):
    nparr = np.frombuffer(image_file, np.uint8)
    cv2.normalize(nparr, None, 255, 0, cv2.NORM_MINMAX, cv2.CV_8U)

    image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    return image

@app.route('/deepforest/<path:model>', methods=['POST'])
def get_image(model):
    print("get free samples for "+model)

    # if 'image' not in request.files:
    #     return 'No image provided', 400

    image_file = request.data
    print(image_file)
    img = read_image_opencv(image_file)
    # if(model=='TreeDetectionDeepforest'):
    image = treeDetection(img)
    # else:
    #     image = treeDetectionCustom(img)

    # Encode the image to bytes
    image = image[..., ::-1] 

    _, img_encoded = cv2.imencode('.tif', image)

    return send_file(io.BytesIO(img_encoded.tobytes()), mimetype='image/tiff')


# @app.route('/deepforest', methods=['POST'])
# def get_image():
#     # if 'image' not in request.files:
#     #     return 'No image provided', 400

#     image_file = request.data
#     print(image_file)
#     img = read_image_opencv(image_file)
#     image = treeDetection(img)
#     # Encode the image to bytes
#     image = image[..., ::-1] 

#     _, img_encoded = cv2.imencode('.tif', image)

#     return send_file(io.BytesIO(img_encoded.tobytes()), mimetype='image/tiff')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8081)
