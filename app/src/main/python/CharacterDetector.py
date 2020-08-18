import numpy
import cv2
import base64
import pandas
from os.path import dirname, join
import io
from PIL import Image


def main(data):
    decoded_data = base64.b64decode(data)
    np_data = numpy.fromstring(decoded_data, numpy.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    parameters = get_parameters()

    img = fix_image(img)

    A = forward_prop(parameters, img)

    number = numpy.argmax(A)

    return str(number)


def main2(data):
    decoded_data = base64.b64decode(data)
    np_data = numpy.fromstring(decoded_data, numpy.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    _, img = cv2.threshold(img, 80, 255, cv2.THRESH_BINARY_INV)

    img = img.astype(numpy.uint8)

    pil_im = Image.fromarray(img)

    buff = io.BytesIO()
    pil_im.save(buff, format = "PNG")
    img_str = base64.b64decode(buff.getvalue())
    return str(img_str)


def number_exist(x):
    return numpy.sum(x) != 0


def fix_image(x):
    print(x.shape)
    x = cv2.cvtColor(x, cv2.COLOR_BGR2GRAY)
    _, x = cv2.threshold(x, 80, 255, cv2.THRESH_BINARY_INV)

    # part 1: up
    top, bottom = 0, 0
    for i in range(len(x)):
        check = number_exist(x[i])
        if check:
            top = i
            break

    # part 2: below
    for i in reversed(range(len(x))):
        check = number_exist(x[i])
        if check:
            bottom = i
            break

    x = x[top:bottom]

    # part 3: left
    left, right = 0, 0
    x = x.T
    for i in range(len(x)):
        check = number_exist(x[i])
        if check:
            left = i
            break

    # part 4: right
    for i in reversed(range(len(x))):
        check = number_exist(x[i])
        if check:
            right = i
            break

    # part 5: reducing x
    x = x[left: right]
    x = x.T

    # part 6: scaling x
    width = right - left
    height = bottom - top

    print(x.shape)

    x = numpy.concatenate((numpy.zeros((height, 20)), x), axis=1)
    x = numpy.concatenate((x, numpy.zeros((height, 20))), axis=1)
    x = numpy.concatenate((numpy.zeros((20, width + 40)), x), axis=0)
    x = numpy.concatenate((x, numpy.zeros((20, width + 40))), axis=0)

    print(x.shape)

    x = cv2.resize(x, (28, 28))
    print(x.shape)
    x = x.reshape((784, 1))
    print(x.shape)

    x /= 255

    return x


def get_parameters():
    parameters = {}
    for l in range(1, 4):
        filename = join(dirname(__file__), 'params W' + str(l) + '.csv')
        parameters['W' + str(l)] = numpy.array(pandas.read_csv(filename).values)
        filename = join(dirname(__file__), 'params b' + str(l) + '.csv')
        parameters['b' + str(l)] = numpy.array(pandas.read_csv(filename).values)
    return parameters


def relu(Z):
    return numpy.maximum(0, Z)


def sigmoid(Z):
    return 1 / (1 + numpy.exp(-Z))


def linear_jump(A_prev, W, b):
    return numpy.dot(W, A_prev) + b


def forward_prop(parameters, X):
    L = int(len(parameters) / 2)
    A_prev = X

    for l in range(1, L):
        Z = linear_jump(A_prev, parameters["W" + str(l)], parameters["b" + str(l)])
        A_prev = relu(Z)

    Z_final = linear_jump(A_prev, parameters["W" + str(L)], parameters["b" + str(L)])
    AL = sigmoid(Z_final)

    return AL




