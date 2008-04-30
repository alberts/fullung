function [E,S]=nap(D, k)
% make tolerance larger for quicker operation
options.tol = 1.0e-6;
% problem should be solved in less than 200 iterations
options.maxit = 200;  
options.issym = 1;
options.disp = 1;

n = size(D,2);

% calculate eigenvectors of D'*D
[E, S] = eigs(@(x)mult(D,x),n,k,'LM',options);

% scale eigenvalues afterwards instead of scaling D
S = diag(S)/n;

% convert to eigenvectors of D*D'
E = D*E;

% orthogonalize final vectors
[E,ignore,ignore] = svd(E, 0); %#ok<NASGU>

function y = mult(D, x)
% calculate D'*D * v
y = ((D*x)'*D)';
