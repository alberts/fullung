load tar.txt;
load non.txt;
load tar2.txt;
load non2.txt;
load tar3.txt;
load non3.txt;

figure;
hold on;
plot((1:1:length(tar))/length(tar),sort(tar),'b',(1:1:length(non))/length(non),sort(non),'r');
plot((1:1:length(tar2))/length(tar2),sort(tar2),'b-.',(1:1:length(non2))/length(non2),sort(non2),'r-.');
plot((1:1:length(tar3))/length(tar3),sort(tar3),'b:',(1:1:length(non3))/length(non3),sort(non3),'r:');
hold off;
