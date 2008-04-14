function gmmstuff

N = 1;
D = 1;
C = 1;
data = zeros(N, D, 'single');
bayesS = struct();
bayesS.weight = ones(C, 1, 'single')/C;
bayesS.mu = zeros(D, C, 'single');
bayesS.sigma = zeros(D, D, C, 'single');

for i=1:1:C
    bayesS.sigma(:,:,i) = diag(ones(D, 1));
end

% bayesS.weight
% bayesS.mu
% bayesS.sigma

tic;
p = gmmb_pdf(data, bayesS);
p
toc;
