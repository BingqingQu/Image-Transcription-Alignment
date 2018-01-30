# Bingqing Qu, June 2017
# Re-order the text line from top to down (original is from left to right)


import re
import sys
import os
import shutil
import argparse

from bs4 import BeautifulSoup


# ----------------------------------------- read hOCR file -----------------------------
def hocr_to_lines(hocr_path):
    lines = []
    soup = BeautifulSoup(file(hocr_path))
    i = 0
    for tag in soup.select('.ocr_line'):
        m = re.match(r'(-?\d+) (-?\d+) (-?\d+) (-?\d+)', tag.get('title'))
        # m = re.match(r'bbox (-?\d+) (-?\d+) (-?\d+) (-?\d+)', tag.get('title'))
        assert m
        x0, y0, x1, y1 = (int(v) for v in m.groups())
        lines.append({
            'x0': x0,
            'y0': y0,
            'x1': x1,
            'y1': y1,
            'text': tag.text
        })
        print 'x0,y0,x1,y1:', x0,y0,x1,y1
        print 'text:', tag.text

    return lines


# -------------------------------------- find the correct order of the line images -------------------------
def sort_lines(lines):
    # delete the lines which may be out of the bounding box of bloc
    X0 = sum(line['x0'] for line in lines)  # sum of the x coordinates of each line
    X1 = sum(line['x1'] for line in lines)
    Y0 = min(line['y1'] for line in lines)  # min and max y coordinates
    Y1 = max(line['y1'] for line in lines)
    L = (X1-X0)/len(lines)  # average length of a line
    H = (Y1-Y0)/len(lines)  # average height of a line

    print (L, H)

    # verify each line: if it is out of the text block, it should be out of the order
    lines_sort1 = []
    for line in lines:
        if line['x1'] < (X0 - line['x0']) / (len(lines) - 1) or line['x0'] > (X1 - line['x1']) / (len(lines) - 1) or line['y1'] < Y0 or line['y0'] > Y1:
            lines_sort1 = lines_sort1
        else:
            lines_sort1.append(line)

    # if it is shorter than 1/4 of the length of the line, it should be out of the order
    lines_sort2 = []
    for line in lines_sort1:
        if (line['x1'] - line['x0']) < L/4 or (line['y1'] - line['y0']) < H/3 or (line['y1'] - line['y0']) > 2*H:
            lines_sort2 = lines_sort2
        else:
            lines_sort2.append(line)
    print len(lines_sort2)
    lines_sort = lines_sort2
    return lines_sort


# ----------------------------------- re-order and re-save the line images in a new folder --------------------------
def re_order(lines_sort, path_input, path_output):
    txt_files = [os.path.join(path_input, item) for item in os.listdir(path_input) if item[-3:] == 'txt']
    txt_content = []

    for item in txt_files:
        with open(item, 'r') as f:
            tmp = f.read().splitlines()
        txt_content.append([item, tmp])

    for i, item in enumerate(lines_sort):
        tmp = item['text']
        tmp_str = str(tmp).replace("\\","")
        for j in range(len(txt_content)):
            if txt_content[j][1][0] == tmp_str:
                #import pdb; pdb.set_trace()
                src_txt = txt_content[j][0]
                src_png = txt_content[j][0][:-3] + 'bin.png'
                if i+1 < 10:
                    dst_txt = os.path.join(path_output, '{}{}.txt'.format('0', i + 1))
                    dst_png = os.path.join(path_output, '{}{}.bin.png'.format('0', i + 1))
                else:
                    dst_txt = os.path.join(path_output, '{}.txt'.format(i + 1))
                    dst_png = os.path.join(path_output, '{}.bin.png'.format(i + 1))
                shutil.copy(src_txt, dst_txt)
                shutil.copy(src_png, dst_png)



if __name__ == '__main__':
    # _, hocr_path = sys.argv
    parser = argparse.ArgumentParser()
    parser.add_argument('-b','--path_hocr', type=str, help='PATH TO HOCR')
    parser.add_argument('-i','--path_in', type=str, help='PATH TO INPUT FOLDER')
    parser.add_argument('-o','--path_out', type=str, help='PATH TO OUTPUT FOLDER')
    args = parser.parse_args()

    hocr_path = args.path_hocr
    path_input = args.path_in
    path_output = args.path_out

    lines = hocr_to_lines(hocr_path)
    lines_sort = sort_lines(lines)
    re_order(lines_sort, path_input, path_output)
