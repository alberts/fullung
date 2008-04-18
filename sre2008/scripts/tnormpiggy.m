function tnormpiggy
% svmh5 = 'z:\data\tnorm79\sre05_eval_svm.h5';
% gmmh5 = 'z:\data\tnorm79\sre05_eval_gmm.h5';
svmh5 = 'z:\data\tnorm79\sre06_eval_svm.h5';
gmmh5 = 'z:\data\tnorm79\sre06_eval_gmm.h5';

if 1
    T = readtnorm('Z:\data\tnorm79\tnorm_svm.h5');
    [U, S, V] = svd(T', 0);
    Utrans = U';
    save tnormstuff.mat T Utrans S V;
    return;
else
    load tnormstuff.mat T Utrans V;
end

n = size(T, 1);
onesV = ones(1, n) * V;

output = 'tnormpiggy06.h5';
hdf5write(output, '/', '');

fid = fopen('sre06-1conv4w_1conv4w-invert.txt');
labels = [];
while 1
    tline = fgetl(fid);
    if ~ischar(tline), break, end
    parts = strsplit(' ', tline);
    trialparts = strsplit(':', parts{1});
    segment = trialparts{1};
    channelstr = trialparts{2};
    channel = strcmp(channelstr,'b');
    name = sprintf('/%s/%d', segment, channel);
    t = [hdf5read(gmmh5, name); -1];
    tnormscores = T * t;
    tnormmean = mean(tnormscores);
    tnormvar = var(tnormscores);
    tnormstddev = sqrt(tnormvar);
    alpha = -1.0 / (n * tnormstddev);
    alphaonesV = alpha * onesV;
    Utranst = (Utrans * t)';
    sCV = (tnormscores - mean(tnormscores))' * V;
    models = strsplit(',', parts{2});
    for modelstr=models
        modelparts = strsplit(':', modelstr{1});
        modelid = modelparts{1};
        labelstr = modelparts{2};
        label = strcmp(labelstr,'targ');
        model = hdf5read(svmh5,sprintf('/%s',modelid))';
        score = model * t;
        score0 = (score - tnormmean)/tnormstddev;
        beta = -score0 / tnormvar;
        piggyscores = Utranst .* (alphaonesV + beta*sCV); 
        scores = [score0 piggyscores];
        disp(sprintf('%s %s %s %.15f %s', modelid, segment, channelstr, score0, labelstr));
        outname = sprintf('/scores/%d', length(labels));
        hdf5write(output, outname, single(scores), 'WriteMode', 'append');
        labels = [labels; label];
    end
end
fclose(fid);
hdf5write(output, '/labels', single(labels), 'WriteMode', 'append');

function [Y] = center(X)
Y = zeros(size(X));
u = mean(X, 2);
for i=1:1:size(X, 2)
    Y(:,i) = X(:,i) - u;
end

function [T]=readtnorm(tnormfile)
info = hdf5info(tnormfile);
n = length(info.GroupHierarchy.Datasets);
T = [];
i = 1;
for dataset=info.GroupHierarchy.Datasets
    x = hdf5read(tnormfile,dataset.Name);
    if isempty(T)
        T = zeros(n,length(x));
    end
    T(i,:) = x;
    i = i + 1;
end
