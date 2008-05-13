%% Setup
sys_indices = [1 2];
% use channel and gender sideinfo when calibrating each system
calibrate_indices = {3:5,9:10};
% use language sideinfo when fusing systems
fuse_indices = 6:8;
lambda = 0;
prior = effective_prior(0.01,10,1);

h5file = 'cvscores.h5';
scores = hdf5read(h5file, '/scores');
info = hdf5info(h5file);
speakers = info.GroupHierarchy.Groups;
speakers = speakers(1:4);

sysscores = scores(sys_indices,:);
calibrate_sideinfos = cell(1, length(calibrate_indices));
for i=1:length(calibrate_indices)
    calibrate_sideinfos{i} = scores(calibrate_indices{i},:);
end
fuse_sideinfo = scores(fuse_indices,:);

nsys = length(sys_indices);
sysweights = cell(1, nsys);
for i=1:nsys
    fprintf('--- Calibrating system %d with sideinfo ---\n\n', i);
    sysweights{i} = calibratesys(sysscores(i,:),calibrate_sideinfos,lambda,prior,speakers,h5file);
end

fprintf('--- Training fusion ---\n\n');
weights = fusesys(sysscores,calibrate_sideinfos,sysweights,fuse_sideinfo,lambda,prior,speakers,h5file);

%% Testing
fused_tar = [];
fused_non = [];
for s = 1:1:length(speakers)
    % read original test scores
    name = speakers(s).Name;
    try
        tar_indices = hdf5read(h5file, sprintf('%s/test/target', name));
    catch me
        tar_indices = [];
    end
    try
        non_indices = hdf5read(h5file, sprintf('%s/test/nontarget', name));
    catch me
        non_indices = [];
    end

    % get system scores
    tar = sysscores(:,tar_indices);
    non = sysscores(:,non_indices);

    % get calibration sideinfo
    tar_sideinfos = cell(1, length(calibrate_sideinfos));
    for j=1:1:length(tar_sideinfos)
        tar_sideinfos{j} = calibrate_sideinfos{j}(:,tar_indices);
    end
    non_sideinfos = cell(1, length(calibrate_sideinfos));
    for j=1:1:length(non_sideinfos)
        non_sideinfos{j} = calibrate_sideinfos{j}(:,non_indices);
    end

    % get fusion sideinfo
    tar_fuse_sideinfo = fuse_sideinfo(:,tar_indices);
    non_fuse_sideinfo = fuse_sideinfo(:,non_indices);

    % get calibration weights for speaker
    for i=1:length(sysweights)
        sysweights_speaker = cell(1, length(sysweights{i}));
        for j=1:1:length(sysweights_speaker)
            sysweights_speaker{j} = sysweights{i}{j}{s};
        end
    end
    
    fprintf('bork\n');
    return

    % get all system weights for this speaker
    sysweights_speaker = cell(1, length(sysweights));
    for j=1:1:length(sysweights_speaker)
        sysweights_speaker{j} = sysweights{i}{j}{s};
    end

    fused_tar = [fused_tar apply_complete_fusion(sysweights_speaker,weights{s},tar,tar_sideinfos,tar_fuse_sideinfo)];
    fused_non = [fused_non apply_complete_fusion(sysweights_speaker,weights{s},non,non_sideinfos,non_fuse_sideinfo)];
end

%% Average to get final stage weights
sysweightsFinal = cell(1, length(sysweights));
% loop over system weights
for i=1:1:length(sysweights)
    sysweight = [];
    % loop over system weights for speakers
    for j=1:length(sysweights{i})
        if isempty(sysweight)
            sysweight = sysweights{i}{j};
        else
            sysweight = sysweight + sysweights{i}{j};
        end
    end
    sysweight = sysweight / length(sysweights{i});
    fprintf('Weights for system %d:\n', i);
    disp(sysweight);
    sysweightsFinal{i} = sysweight;
end
weightsFinal = [];
for i=1:1:length(weights)
    if isempty(weightsFinal)
        weightsFinal = weights{i};
    else
        weightsFinal = weightsFinal + weights{i};
    end
end
weightsFinal = weightsFinal/length(weights);
save fusion.mat weightsFinal sysweightsFinal sys_indices calibrate_indices fuse_indices;

%% Sanity check
tar = hdf5read(h5file, '/target');
non = hdf5read(h5file, '/nontarget');
tar_sideinfos = cell(1, length(calibrate_indices));
for j=1:1:length(tar_sideinfos)
    tar_sideinfos{j} = tar(:,calibrate_indices);
end
non_sideinfos = cell(1, length(calibrate_indices));
for j=1:1:length(non_sideinfos)
    non_sideinfos{j} = non(:,calibrate_indices);
end
sanity_tar = apply_complete_fusion(sysweightsFinal,weightsFinal,tar,tar_sideinfos,tar(:,fuse_indices));
sanity_non = apply_complete_fusion(sysweightsFinal,weightsFinal,non,non_sideinfos,non(:,fuse_indices));

%% DET Plot
figure;
hold on;
colors = 'rgbcmyk';
for i=systems
    plotdet(tar(i,:), non(i,:), colors(i));
end
plotdet(fused_tar, fused_non, 'k--');
plotdet(sanity_tar, sanity_non, 'k-.');
hold off;

%% APE Plot (scores must be LLRs)
% figure;
% ape_plot({'fusion',{fused_tar,fused_non}}, {'sanity',{sanity_tar,sanity_non}});
