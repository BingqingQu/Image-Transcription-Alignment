# Bingqing Qu, June 2017
# copy grounth files into a new folder and rename them


import re
import sys
import os
import shutil
import argparse

def Renanme(page_image, GT_path, Des_path):

    txt_files = [os.path.join(GT_path, item) for item in os.listdir(GT_path) if item[0:10] == page_image]

    txt_content = []
    for item in txt_files:
        txt_content.append(item)  # item: file name;

    for i, item in enumerate(txt_content):
        src_txt = txt_content[i]
        if i+1 < 10:
            dst_txt = os.path.join(Des_path, '{}{}.gt.txt'.format('0', i + 1))
        else:
            dst_txt = os.path.join(Des_path, '{}.gt.txt'.format(i + 1))
        shutil.copy(src_txt, dst_txt)



if __name__ == '__main__':
    # _, hocr_path = sys.argv
    parser = argparse.ArgumentParser()
    parser.add_argument('-i','--img_name', type=str, help='NAME OF PAGE IMAGE')
    parser.add_argument('-g','--path_gt', type=str, help='PATH TO GROUND TRUTH')
    parser.add_argument('-d','--path_des', type=str, help='PATH TO DESTINATION')
    args = parser.parse_args()

    page_image = args.img_name
    GT_path = args.path_gt
    Des_path = args.path_des

    Renanme(page_image,GT_path, Des_path)
