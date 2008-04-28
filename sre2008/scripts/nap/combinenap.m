function combinenap
% number of eigenvectors
k1 = 40;
k2 = 40;

load S1.mat S1;
load S2.mat S2;

load E1.mat E1;
load E2.mat E2;

% todo call orth

% U = zeros(size(E));
% for i=1:1:length(S)
%     U(:,i) = sqrt(S(i)) * E(:,i);
% end

% hdf5write('channel.h5', '/U', U');
