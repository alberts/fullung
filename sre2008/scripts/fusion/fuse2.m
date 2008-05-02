h5file = 'cvscores.h5';
prior = effective_prior(0.01,10,1);

info = hdf5info(h5file);
weights = zeros(2, length(info.GroupHierarchy.Groups));
count = 1;
fused_targ = [];
fused_non = [];
for group = info.GroupHierarchy.Groups
    name = group.Name;
    if strfind(name, '/spk') ~= 1
        continue
    end
    disp(name);
    train_targ = hdf5read(h5file, sprintf('%s/train/targ', name));
    train_targ = train_targ(:)';
    train_non = hdf5read(h5file, sprintf('%s/train/non', name));
    train_non = train_non(:)';

    w = train_llr_fusion(train_targ,train_non,prior);
    weights(:, count) = w;
    count = count + 1;

    try
        test_targ = hdf5read(h5file, sprintf('%s/test/targ', name));
        test_targ = test_targ(:)';
    catch me
        test_targ = [];
    end
    try
        test_non = hdf5read(h5file, sprintf('%s/test/non', name));
        test_non = test_non(:)';
    catch me
        test_non = [];
    end

    % apply fusion to trials for speaker
    if ~isempty(test_targ)
        fused_targ = [fused_targ lr_fusion(w, test_targ)];
    end
    if ~isempty(test_non) > 0
        fused_non = [fused_non lr_fusion(w, test_non)];
    end
end

% convert to llrs for ape_plot
fused_targ = log(fused_targ);
fused_non = log(fused_non);

orig_targ = hdf5read(h5file, '/targ');
orig_targ = orig_targ(:)';
orig_non = hdf5read(h5file, '/non');
orig_non = orig_non(:)';

% sanity check
w = mean(weights, 2);
sanity_targ = log(lr_fusion(w, orig_targ));
sanity_non = log(lr_fusion(w, orig_non));

figure;
hold on;
plotdet(orig_targ, orig_non, 'r');
hold off;

figure;
ape_plot({'orig',{orig_targ,orig_non}}, {'fusion',{fused_targ,fused_non}}, {'sanity',{sanity_targ,sanity_non}});
