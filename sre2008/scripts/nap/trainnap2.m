function trainnap2
dim = 512 * 38;
naph5 = 'Z:\data\nap512v2\nap_gmm.h5';
% number of eigenvectors
k = 100;

phnmodels = {};
micmodels = {};
modelcount = 0;
Nmicses = 0;
fid = fopen('nap2005_phnmic.txt');
while 1
    tline = fgetl(fid);
    if ~ischar(tline), break, end
    parts = strsplit(' ', tline);
    phn = strsplit(',', parts{4});
    mic = strsplit(',', parts{5});
    modelcount = modelcount + 1;
    phnmodels{modelcount} = phn; %#ok<AGROW>
    micmodels{modelcount} = mic; %#ok<AGROW>
    Nmicses = Nmicses + length(mic);
end
fclose(fid);

if 1
    D = zeros(dim, Nmicses, 'single');
    index = 1;
    for i=1:1:length(phnmodels)
        disp(i);
        phn = phnmodels{i};
        phndata = zeros(dim, length(phn));
        for j = 1:1:length(phn)
            hdfname = gethdfname(phn{j});
%             fprintf('phn <- %s\n', hdfname);
            phndata(:, j) = hdf5read(naph5, hdfname);
        end
        phnmean = mean(phndata, 2);
        mic = micmodels{i};
        step = length(mic);
        for j = 1:1:step
            hdfname = gethdfname(mic{j});
%             fprintf('mic <- %s\n', hdfname);
            D(:,index+j-1) = hdf5read(naph5, hdfname);
        end
        indices = index:index+step-1;
        Dpart = D(:,indices);
        D(:,indices) = Dpart - repmat(phnmean, 1, step);
        index = index + step;
    end
%     save D2.mat D;
    return;
else
    load D2.mat D;
end

[E, S] = nap(D, k);
save S2.mat S;
U = zeros(size(E));
for i=1:1:length(S)
    U(:,i) = sqrt(S(i)) * E(:,i);
end
% U*U' is almost equal to D*D'/n
save E2.mat E;
save U2.mat U;
% transpose matrix to write it in C order
hdf5write('channel2.h5', '/U', U');

function [hdfname]=gethdfname(namechannel)
parts = strsplit(':', namechannel);
name = parts{1};
channel = parts{2}-'a';
hdfname = sprintf('/%s/%d', name, channel);
