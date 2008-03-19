cd 2005
eval2005.py 2>..\sre05xml.log >..\sre2005_v1.xml
cd ..\2006
eval2006.py 2>..\sre06xml.log >..\sre2006_v1.xml
cd ..
extracteval.py sre2005_v1.xml 1conv4w 1conv4w > sre05-1conv4w_1conv4w.txt
extracteval.py sre2006_v1.xml 1conv4w 1conv4w > sre06-1conv4w_1conv4w.txt
