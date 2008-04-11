function preppiggy
piggyfile = 'piggyback.h5';
labels = hdf5read(piggyfile,'/labels');

% original scores, piggyback scores, rho
scoredim = 1 + 512*38 + 1;

X = zeros(scoredim,length(labels),'single');
for i=1:1:length(labels)
    scores = hdf5read(piggyfile,sprintf('/scores/%d',i-1));
    X(:,i) = scores(:);
end

scores = X(1,:);
Y = X(2:end,:);
gram = Y'*Y;

save piggy.mat labels scores gram -V6;
