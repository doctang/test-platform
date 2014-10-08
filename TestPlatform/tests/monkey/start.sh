#!/bin/sh

#获得原始的第三方包
pm list package -3 > monkey/tmp.txt

#清理黑名单列表文件
rm -f monkey/blacklist.txt

#截取正确的包名
while read line
do
    echo ${line:8} >> monkey/blacklist.txt
done < monkey/tmp.txt

#修改文件权限
chmod 644 monkey/blacklist.txt

#清理临时文件
rm monkey/tmp.txt

#执行
monkey --pkg-blacklist-file monkey/blacklist.txt $1 > $2".txt" 2>&1

#修改文件权限
chmod 644 $2".txt"
