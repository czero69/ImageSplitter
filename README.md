ImageSplitter
=============

v0.01 a.

Splits 16 bit per channel image into two 8 bit per channel image. Useful wehen working staff like webgl and want's use 16 bit per channel.
This example .java works for a catalog of 48 bit tif (101x101) and splits each of them into two 24 bit png.
cm.png (24 bit) (101x101) is used for colormodel and WritableRaster data.

If you want splitting image in another size change cm.png size.
If you want to split another format to another format ex. tif to tif or png to png or/and you wudl like to use alpha canal you must make simple changes in the code.

used libs: JAI, Common I/O.

credits:
based on http://www.codeproject.com/Articles/456525/Java-48-Bit-TIFF-Image-Processing