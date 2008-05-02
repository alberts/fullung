function [x] = clip(x, thresh)
thresh=abs(thresh);
x(x>thresh)=thresh;
x(x<-thresh)=-thresh;
