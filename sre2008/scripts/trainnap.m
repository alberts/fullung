function napstuff
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

global D;
if 0
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
    load D.mat;
end

size(D)
% looks like this plots gender info using our baseline UBM
%plot(D(1,:));
%plot(D(2,:));

% number of eigenvectors
k = 40;
% make tolerance larger for quicker operation
options.tol = 1.0e-6;
% problem should be solved in less than 200 iterations
options.maxit = 200;  
options.issym = 1;
% calcuylate eigenvectors of D'*D
[V, S] = eigs(@Dmult,size(D,2),k,'LM',options);

diag(S)

size(V)

% convert to eigenvectors of D*D'
V = D*V;

size(V)

% XXX do we need to normalize sizes of V here?

% orthogonalize
[E,SS,VV] = svd(V, 0);

save E.mat E;

function prod = Dmult(v)
global D;
% calculate D'*D * v
prod = ((D*v)'*D)';
