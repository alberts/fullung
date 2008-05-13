%% Setup
lambda = 400;
prior = effective_prior(0.01,10,1);

systems = [1 2 3 4 5 6 7];
sidestages = {8:10,11:13,14:15};

h5file = 'cvscores.h5';
info = hdf5info(h5file);
speakers = info.GroupHierarchy.Groups;
speakers = speakers(randperm(length(speakers)));
speakers = speakers(1:10);

%% Training
scores = hdf5read(h5file, '/scores');
if isempty(sidestages); sidestages = {[]}; end
weights = {};
for i=1:length(sidestages)
    fprintf('=== Stage %d of %d: begin ===\n', i, length(sidestages));
    sidestage = sidestages{i};
    w = {};
    tic;
    spkcount = 0;
    for speaker = speakers
        spkcount = spkcount + 1;
        % read train trials
        name = speaker.Name;
        tar_indices = hdf5read(h5file, sprintf('%s/train/target', name));
        tar = scores(:,tar_indices);
        train_scores_tar = tar(systems,:);
        non_indices = hdf5read(h5file, sprintf('%s/train/nontarget', name));
        % lookup actual scores from indices
        non = scores(:,non_indices);
        train_scores_non = non(systems,:);

        % apply all fusions up to this point
        for j=1:i-1
            % get weight matrix at stage j for this speaker
            ww = weights{j}{spkcount};
            train_scores_tar = apply_bilinear_fusion(ww,train_scores_tar,tar(sidestages{j},:));
            train_scores_non = apply_bilinear_fusion(ww,train_scores_non,non(sidestages{j},:));
        end

        % get sideinfo for this stage
        train_side_tar = tar(sidestage,:);        
        train_side_non = non(sidestage,:);        

        % train stage weights for this speaker
        fprintf('Training %s [speaker %d/%d, stage %d/%d]: #tar=%d, #non=%d\n', ...
            name, spkcount, length(speakers), i, length(sidestages), size(tar,2), size(non,2));
        ww = train_sideinfo_fusion(train_scores_tar,train_side_tar,train_scores_non,train_side_non,lambda,prior);
        w = {w{:} ww};
        fprintf('Training time: %.8f seconds\n\n', toc/spkcount);
        fprintf('Weights for this speaker:\n'); disp(ww); fprintf('\n');
    end
    weights = {weights{:} w};

    fprintf('=== Stage %d of %d done. Training time: %.8f seconds ===\n\n', i, length(sidestages), toc);
end

%% Testing
fused_tar = [];
fused_non = [];
spkcount = 0;
for speaker = speakers
    spkcount = spkcount + 1;
    % read original test scores
    name = speaker.Name;
    try
        tar_indices = hdf5read(h5file, sprintf('%s/test/target', name));
        tar = scores(:,tar_indices);
        test_scores_tar = tar(systems,:);
    catch me
        tar = [];
        test_scores_tar = [];
    end
    try
        non_indices = hdf5read(h5file, sprintf('%s/test/nontarget', name));
        non = scores(:,non_indices);
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
        ww = weights{i}{spkcount};

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

%% Average to get final stage weights
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
    fprintf('Weights for stage %d:\n', i);
    disp(stageW);
    finalW = {finalW{:} stageW};
end
save fusion.mat finalW systems sidestages;

%% Sanity check
tar = hdf5read(h5file, '/target');
non = hdf5read(h5file, '/nontarget');

sanity_tar = apply_bilinear_fusions(finalW,tar,systems,sidestages);
sanity_non = apply_bilinear_fusions(finalW,non,systems,sidestages);

%% DET Plot
figure;
hold on;
% colors = 'rgbcmyk';
% for i=systems
%     plotdet(tar(i,:), non(i,:), colors(i));
% end
plotdet(fused_tar, fused_non, 'k');
plotdet(sanity_tar, sanity_non, 'k-.');
hold off;

%% APE Plot (scores must be LLRs)
% figure;
% ape_plot({'fusion',{fused_tar,fused_non}}, {'sanity',{sanity_tar,sanity_non}});
