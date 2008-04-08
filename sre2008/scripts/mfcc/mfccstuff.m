function mfccstuff

sphfilename = 'jabo.sph';
%sphfilename = 'kajx.sph';
%sphfilename = 'xdac.sph';
mfccperiod = 10e-3;

[y, fs, ffx] = readsph(sphfilename, 'r');
period = 1/fs;
y = y + 128;
y = mu2lin(y);
y = y * 32768;
t1 = 0:period:(size(y, 1)-1)*period;
n = size(y, 2);

% figure;
% t2 = 0:mfccperiod:(size(y, 1) * period);
% for i=1:1:n
%     subplot(n, 1, i);
%     % original speech
%     z = y(:,i);
%     mfcfilename = sprintf('%s.%d.mfc', sphfilename, i - 1);
%     % mfcc features with indexes appended
%     [mfcc,fp,dt,tc,t]=readhtk(mfcfilename);
%     speechidx = mfcc(:,end)+1;    
%     mfcc = mfcc(:,1:end-1);
%     disp(size(mfcc));
%     vad = zeros(1,length(t2));
%     vad(speechidx) = 1;
%     plot(t1, z, 'r', t2, 10000*vad, 'b');
%     xlabel('t [s]');
%     %xlim([0 30]);
%     xlim([0 30]);
%     ylim([-20000 20000]);
%     grid on;
%     grid minor;
% end

[mfcc,fp,dt,tc,t]=readhtk('xdac.sph.0.mfc');
% size(mfcc)
mfcc=mfcc(:,1:end-1);
figure;
hold on;
for i=1:1:size(mfcc, 2)
    disp([min(mfcc(:,i)) max(mfcc(:,i))]);
    plot(mfcc(:,i)-(i-1)*6);
end
hold off;
% 
% figure;
% hist(mfcc(:,30),20);
