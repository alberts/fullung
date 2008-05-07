%% Setup
scoresys = [1 2 3 4];
%put sideinfo stages into cell array, e.g. {4 5:7}
sidesys = 5:7;
h5file = 'cvscores.h5';
info = hdf5info(h5file);
prior = effective_prior(0.01,10,1);

%% Cross-validation fusion
weights = {};
count = 0;
fused_tar = [];
fused_non = [];
groups = info.GroupHierarchy.Groups;
maxcount = length(groups);
tic;
for group = groups
    count = count + 1;
    name = group.Name;
    if strfind(name, '/spk') ~= 1
        continue
    end

    % read train trials
    tar = hdf5read(h5file, sprintf('%s/train/target', name));
    train_scores_tar = tar(scoresys,:);
    train_side_tar = tar(sidesys,:);
    non = hdf5read(h5file, sprintf('%s/train/nontarget', name));
    train_scores_non = non(scoresys,:);
    train_side_non = non(sidesys,:);

    % train fusion
    fprintf('training %s [%d of %d]: #tar=%d, #non=%d\n', name, count, maxcount, size(tar,2), size(non,2));
    w = train_sideinfo_fusion(train_scores_tar,train_side_tar,train_scores_non,train_side_non);
    weights = {weights{:} w};

    % read test trials
    try
        tar = hdf5read(h5file, sprintf('%s/test/target', name));
        test_scores_tar = tar(scoresys,:);
        test_side_tar = tar(sidesys,:);
    catch me
        test_scores_tar = [];
        test_side_tar = [];
    end
    try
        non = hdf5read(h5file, sprintf('%s/test/nontarget', name));
        test_scores_non = non(scoresys,:);
        test_side_non = non(sidesys,:);
    catch me
        test_scores_non = [];
        test_side_non = [];
    end

    % apply fusion to trials for speaker
    if ~isempty(test_scores_tar)
        fused_tar = [fused_tar apply_bilinear_fusion(w,test_scores_tar,test_side_tar)]; %#ok<AGROW>
    end
    if ~isempty(test_scores_non)
        fused_non = [fused_non apply_bilinear_fusion(w,test_scores_non,test_side_non)]; %#ok<AGROW>
    end
    fprintf('Average training time: %.8f seconds\n', toc/count);
end
fprintf('Total training time: %.8f seconds\n', toc);

%% Final weights
w = [];
for i=1:1:length(weights)
    if isempty(w)
        w = weights{i};
    else
        w = w + weights{i};
    end
end
w = w / length(weights);

%% Sanity check
tar = hdf5read(h5file, '/target');
orig_scores_tar = tar(scoresys,:);
orig_side_tar = tar(sidesys,:);

non = hdf5read(h5file, '/nontarget');
orig_scores_non = non(scoresys,:);
orig_side_non = non(sidesys,:);

sanity_tar = apply_bilinear_fusion(w,orig_scores_tar,orig_side_tar);
sanity_non = apply_bilinear_fusion(w,orig_scores_non,orig_side_non);

%% DET Plot
colors = 'rgby';
figure;
hold on;
for i=1:size(orig_scores_tar, 1)
    plotdet(orig_scores_tar(i,:), orig_scores_non(i,:), colors(i));
end
plotdet(fused_tar, fused_non, 'k');
plotdet(sanity_tar, sanity_non, 'k-.');
hold off;

%% APE Plot (scores must be LLRs)
figure;
ape_plot({'fusion',{fused_tar,fused_non}}, {'sanity',{sanity_tar,sanity_non}});
