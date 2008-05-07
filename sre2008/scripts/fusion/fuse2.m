%% Setup
systems = [1 2 3 4];
%sidestages = {5:7,8:10,11:12};
sidestages = {5:7,11:12};
h5file = 'cvscores.h5';
info = hdf5info(h5file);
prior = effective_prior(0.01,10,1);
groups = info.GroupHierarchy.Groups;

groups = groups(1:2);

maxcount = length(groups);

%% Training
weights = {};
tic;
for i=1:length(sidestages)
    fprintf('=== Stage %d of %d: begin ===\n', i, length(sidestages));
    
    sidestage = sidestages{i};
    count = 0;
    w = {};
    for group = groups
        count = count + 1;
        name = group.Name;
        if strfind(name, '/spk') ~= 1
            continue
        end

        % read train trials
        tar = hdf5read(h5file, sprintf('%s/train/target', name));
        train_scores_tar = tar(systems,:);
        non = hdf5read(h5file, sprintf('%s/train/nontarget', name));
        train_scores_non = non(systems,:);

        % apply all fusions up to this point
        for j=1:i-1
            % get weight matrix at stage j for this speaker
            ww = weights{j}{count};
            train_scores_tar = apply_bilinear_fusion(ww,train_scores_tar,tar(sidestages{j},:));
            train_scores_non = apply_bilinear_fusion(ww,train_scores_non,non(sidestages{j},:));
        end

        % get sideinfo for this stage
        train_side_tar = tar(sidestage,:);        
        train_side_non = non(sidestage,:);        

        % train stage weights for this speaker
        fprintf('Training %s [stage %d: model %d of %d]: #tar=%d, #non=%d\n', ...
            name, i, count, maxcount, size(tar,2), size(non,2));
        ww = train_sideinfo_fusion(train_scores_tar,train_side_tar,train_scores_non,train_side_non);
        w = {w{:} ww};
        fprintf('Model training time: %.8f seconds\n\n', toc/(count*i));
    end
    weights = {weights{:} w};

    fprintf('=== Stage %d of %d: end ===\n\n', i, length(sidestages));
end
fprintf('Total training time: %.8f seconds\n', toc);

%% Testing
fused_tar = [];
fused_non = [];
for group = groups
    count = 0;
    count = count + 1;
    name = group.Name;
    if strfind(name, '/spk') ~= 1
        continue
    end

    % read original test scores
    try
        tar = hdf5read(h5file, sprintf('%s/test/target', name));
        test_scores_tar = tar(systems,:);
    catch me
        tar = [];
        test_scores_tar = [];
    end
    try
        non = hdf5read(h5file, sprintf('%s/test/nontarget', name));
        test_scores_non = non(systems,:);
    catch me
        non = [];
        test_scores_non = [];
    end

    % apply each fusion stage to test scores in succession
    for i=1:length(sidestages)
        % get sideinfo required by this stage
        sidestage = sidestages{i};
        if ~isempty(tar)
            test_side_tar = tar(sidestage,:);
        else
            test_side_tar = [];
        end
        if ~isempty(non)
            test_side_non = non(sidestage,:);
        else
            test_side_non = [];
        end

        % get weight matrix at stage j for this speaker
        ww = weights{i}{count};

        % apply the fusion at this stage
        if ~isempty(test_scores_tar)
            test_scores_tar = apply_bilinear_fusion(ww,test_scores_tar,test_side_tar);
        end
        if ~isempty(test_scores_non)
            test_scores_non = apply_bilinear_fusion(ww,test_scores_non,test_side_non);
        end
    end
    fused_tar = [fused_tar test_scores_tar]; %#ok<AGROW>
    fused_non = [fused_non test_scores_non]; %#ok<AGROW>
end

%% Average weights to get final stage weights
finalW = {};
for i=1:1:length(weights)
    stageW = [];
    for j=1:length(weights{i})
        if isempty(stageW)
            stageW = weights{i}{j};
        else
            stageW = stageW + weights{i}{j};
        end
    end
    stageW = stageW / length(weights{i});
    finalW = {finalW{:} stageW};
end

%% Sanity check
tar = hdf5read(h5file, '/target');
non = hdf5read(h5file, '/nontarget');

sanity_tar = apply_bilinear_fusions(finalW,tar,systems,sidestages);
sanity_non = apply_bilinear_fusions(finalW,non,systems,sidestages);

%% DET Plot
figure;
hold on;
colors = 'rgby';
for i=systems
    plotdet(tar(i,:), non(i,:), colors(i));
end
plotdet(fused_tar, fused_non, 'k');
plotdet(sanity_tar, sanity_non, 'k-.');
hold off;

%% APE Plot (scores must be LLRs)
% figure;
% ape_plot({'fusion',{fused_tar,fused_non}}, {'sanity',{sanity_tar,sanity_non}});
