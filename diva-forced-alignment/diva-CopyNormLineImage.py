# Bingqing Qu, June 2017
# copy grounth files into a new folder and rename them


import re
import sys
import os
import shutil
import argparse

def Renanme(page_image, GT_path, Des_path):

    img_files = [os.path.join(GT_path, item) for item in sorted(os.listdir(GT_path))]
    print 'all GT:', (img_files)

    img_content = []
    for item in img_files:
        img_content.append(item)  # item: file name;

    for i, item in enumerate(img_content):
        src_img = img_content[i]
        print 'line number:', (i)
        print 'GT:', (src_img)

        if i+1 < 10:
            dst_img = os.path.join(Des_path, '{}{}.png'.format('0', i + 1))
        else:
            dst_img = os.path.join(Des_path, '{}.png'.format(i + 1))

        print 'GT new:', (dst_img)

        shutil.copy(src_img, dst_img)



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
