close all;
clear all;
figure;
hold on;
plotdet('sre05_1conv4w_1conv4w_baseline.txt','r');
plotdet('sre05_1conv4w_1conv4w_nap.txt','b');
plotdet('sre05_1conv4w_1conv4w_2048.txt','k');
plotdet('sre05_1conv4w_1conv4w_fc.txt','g');
plotdet('sre05_1conv4w_1conv4w_fc_w.txt','y');
% plotdet('eval.txt','c');
hold off;

%plotdet('sre06_1conv4w_1conv4w_nap.txt','g');
