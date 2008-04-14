function preppiggy
piggyfile = 'piggy05.h5';
labels = hdf5read(piggyfile,'/labels');

% original scores, piggyback scores (including -rho)
scoredim = 1 + (512*38 + 1);

X = zeros(scoredim,length(labels),'single');
for i=1:1:length(labels)
    scores = hdf5read(piggyfile,sprintf('/scores/%d',i-1));
    X(:,i) = scores(:);
end

% exclude system scores before calculating Gram matrix
Y = X(2:end,:);
gram = Y'*Y;

save piggy.mat gram -V6;
