clear all

D = 2;
C = 5;

% data points are rows
data = [1 1; -1 -1; 0 0; -1 1; 1 -1; 0.5 -0.4; 0.7 0.1];
%[X,Y] = meshgrid(-3:0.05:3);
%data = [X(:) Y(:)];

mean(data, 1)
N = size(data, 1);
cov(data)*(N-1)/N

gmm = struct();
gmm.sigma = zeros(D, D, C);

% % weight must be a column vector
gmm.weight = [0.1 0.1 0.2 0.3 0.3]';
% % transpose because means are columns
gmm.mu = [-1.0 -1.0; -1.0 1.0; 1.0 1.0; 1.0 -1.0; 0.0 0.0]';
gmm.sigma(:,:,1) = diag([1.0 1.0]);
gmm.sigma(:,:,2) = diag([1.0 1.0]);
gmm.sigma(:,:,3) = diag([1.0 1.0]);
gmm.sigma(:,:,4) = diag([1.0 1.0]);
gmm.sigma(:,:,5) = diag([1.0 1.0]);

px = gmmb_pdf(data, gmm);
logpx = log(px);

% calculate posterior probabilities
Pjx = zeros(C, size(data, 1));
for j=1:1:C
    Pj = gmm.weight(j);
    g = struct();
    g.weight = 1.0;
    g.mu = gmm.mu(:, j);
    g.sigma = zeros(D, D, 1);
    g.sigma(:,:,1) = gmm.sigma(:,:,j);
    pxj = gmmb_pdf(data, g);
    Pjx(j, :) = Pj * pxj ./ px;
end
%sum(Pjx, 1)==ones(1, size(data, 1))

%Z = reshape(px, size(X));
%Z = reshape(Pjx(1, :), size(X));
%mesh(X, Y, Z)

sort(Pjx, 1, 'descend')
