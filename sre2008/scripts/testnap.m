function testnap
randn('state',0);
D = randn(5, 3);
n = size(D, 2);

u = mean(D, 2);
for i=1:1:n
    D(:,i) = D(:,i) - u;
end

k = 3;
[E, S] = nap(D, k);

U = zeros(size(E));
for i=1:1:length(S)
    U(:,i) = sqrt(S(i)) * E(:,i);
end

X = D*D'/n;
X

[V,W] = eig(X);
W = sort(diag(W),1,'descend');
W

Xp = U*U';
Xp

S
