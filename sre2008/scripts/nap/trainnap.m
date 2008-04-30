function trainnap
dim = 512 * 38;
naph5 = 'Z:\data\nap512v2\both\nap_gmm.h5';
% number of eigenvectors
k = 100;

models = {};
modelcount = 0;
Nses = 0;
fid = fopen('nap2004.txt');
while 1
    tline = fgetl(fid);
    if ~ischar(tline), break, end
    parts = strsplit(' ', tline);
    parts = strsplit(',', parts{4});
    modelcount = modelcount + 1;
    models{modelcount} = parts; %#ok<AGROW>
    Nses = Nses + length(parts);
end
fclose(fid);

if 0
    D = zeros(dim, Nses, 'single');
    size(D)
    index = 1;
    for i=1:1:length(models)
        disp(i);
        parts = models{i};
        step = length(parts);
        for j = 1:1:step
            partsj = strsplit(':', parts{j});
            name = partsj{1};
            channel = partsj{2}-'a';
            hdfname = sprintf('/%s/%d', name, channel);
            D(:,index+j-1) = hdf5read(naph5, hdfname);
        end
        indices = index:index+step-1;
        Dpart = D(:,indices);
        % subtract speaker mean
        u = mean(Dpart, 2);
        u = repmat(u, 1, step);
        D(:,indices) = Dpart - u;
        index = index + step;
    end
    save D1.mat D;
    return;
else
    load D1.mat D;
end

[E, S] = nap(D, k);
save S1.mat S;
U = zeros(size(E));
for i=1:1:length(S)
    U(:,i) = sqrt(S(i)) * E(:,i);
end
% U*U' is almost equal to D*D'/n
save E1.mat E;
save U1.mat U;
% transpose matrix to write it in C order
hdf5write('channel1.h5', '/U', U');
