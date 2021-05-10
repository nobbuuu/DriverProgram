#!/usr/bin/python
#encoding=utf-8

from PIL import Image
import sys, pytesseract 

def recognize_captcha_image():
    image_file='captcha.jpg'
    if len(sys.argv) > 1:
       image_file = sys.argv[1]

    # 打开图片
    image = Image.open(image_file)
    # image.show()
    # 转为灰度图片
    image_grey = image.convert('L')
    # image_grey.show()
    # 二值化
    table = []
    for i in range(256):
        if i < 140:
            table.append(0)
        else:
            table.append(1)
    image_bi = image_grey.point(table, '1')
    # 识别验证码
    verify_code = pytesseract.image_to_string(image_bi)
    print verify_code
    return verify_code

if __name__ == "__main__":
    recognize_captcha_image()
