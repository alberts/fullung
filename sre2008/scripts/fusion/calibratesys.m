function [weights]=calibratesys(scores,calibrate_sideinfos,lambda,prior,speakers,h5file)
if isempty(calibrate_sideinfos); calibrate_sideinfos = {[]}; end

% calibration weights
weights = cell(1, length(calibrate_sideinfos));

% loop over calibration stages
for i=1:length(calibrate_sideinfos)
    fprintf('=== Stage %d of %d: begin ===\n', i, length(calibrate_sideinfos));
    tic;
    w = cell(1, length(speakers));
    for s = 1:1:length(speakers)
        % read train trials
        name = speakers(s).Name;
        tar_indices = hdf5read(h5file, sprintf('%s/train/target', name));
        tar = scores(:,tar_indices);
        non_indices = hdf5read(h5file, sprintf('%s/train/nontarget', name));
        non = scores(:,non_indices);

        % apply all fusions up to this point
        for j=1:i-1
            % get weight matrix at stage j for this speaker
            ww = weights{j}{s};
            tar = apply_bilinear_fusion(ww,tar,calibrate_sideinfos{j}(:,tar_indices));
            non = apply_bilinear_fusion(ww,non,calibrate_sideinfos{j}(:,non_indices));
        end

        % get sideinfo for this stage
        side_tar = calibrate_sideinfos{i}(:,tar_indices);
        side_non = calibrate_sideinfos{i}(:,non_indices);

        % train stage weights for this speaker
        fprintf('Training %s [speaker %d/%d, stage %d/%d]: #tar=%d, #non=%d\n', ...
            name, s, length(speakers), i, length(calibrate_sideinfos), size(tar,2), size(non,2));
        w{s} = train_sideinfo_fusion(tar, side_tar, non, side_non, lambda, prior);
        fprintf('Training time: %.8f seconds\n\n', toc/s);
        fprintf('System weights for this speaker:\n'); disp(w{s}); fprintf('\n');
    end
    weights{i} = w;
    fprintf('=== Stage %d of %d done. Training time: %.8f seconds ===\n\n', i, length(calibrate_sideinfos), toc);
end
