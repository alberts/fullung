function trainnap
dim = 512 * 38;
naph5 = 'C:\home\albert\SRE2008\data\sre04_nap_gmm.h5';

fid = fopen('2004_1s.txt');

models = {};
modelcount = 1;
Nses = 0;
while 1
    tline = fgetl(fid);
    if ~ischar(tline), break, end
    parts = strsplit(' ', tline);
    parts = parts(3:end);
    models{modelcount} = parts;
    modelcount = modelcount + 1;
    Nses = Nses + length(parts);
end
fclose(fid);

if 1
    D = zeros(dim, Nses, 'single');
    index = 1;
    for i=1:1:length(models)
        disp(i);
        parts = models{i};
        step = length(parts);
        for j = 1:1:step
            name = sprintf('/%s/0', parts{j});
            D(:,index+j-1) = hdf5read(naph5, name);
        end
        indices = index:index+step-1;
        Dpart = D(:,indices);
        % subtract speaker mean
        u = mean(Dpart, 2);
        u = repmat(u, 1, step);
        D(:,indices) = Dpart - u;
        index = index + step;
    end
    save D.mat D;
    return;
else
    load D.mat D;
end

% number of eigenvectors
k = 40;

[E, S] = nap(D, k);

U = zeros(size(E));
for i=1:1:length(S)
    U(:,i) = sqrt(S(i)) * E(:,i);
end

% U*U' is almost equal to D*D'/n

save E.mat E;
save U.mat U;

% transpose matrix to write it in C order
hdf5write('fcu.h5', '/U', U');
