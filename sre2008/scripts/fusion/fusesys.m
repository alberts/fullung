function [weights] = fusesys(scores,sideinfos,sysweights,fuse_sideinfo,lambda,prior,speakers,h5file)
weights = cell(1, length(speakers));
for s = 1:1:length(speakers)
    % read train trials
    name = speakers(s).Name;
    tar_indices = hdf5read(h5file, sprintf('%s/train/target', name));
    tar = scores(:,tar_indices);
    non_indices = hdf5read(h5file, sprintf('%s/train/nontarget', name));
    non = scores(:,non_indices);

    tar_sideinfos = cell(1, length(sideinfos));
    for j=1:1:length(tar_sideinfos)
        tar_sideinfos{j} = sideinfos{j}(:,tar_indices);
    end
    non_sideinfos = cell(1, length(sideinfos));
    for j=1:1:length(non_sideinfos)
        non_sideinfos{j} = sideinfos{j}(:,non_indices);
    end

    % apply each system's calibration weights for this speaker
    for i=1:length(sysweights)
        % get a single system's weights for this speaker
        sysweights_speaker = cell(1, length(sysweights{i}));
        for j=1:1:length(sysweights{i})
            sysweights_speaker{j} = sysweights{i}{j}{s};
        end
        tar(i,:) = apply_bilinear_fusions(sysweights_speaker,tar(i,:),tar_sideinfos);
        non(i,:) = apply_bilinear_fusions(sysweights_speaker,non(i,:),non_sideinfos);
    end

    side_tar = fuse_sideinfo(:,tar_indices);
	side_non = fuse_sideinfo(:,non_indices);

    weights{s} = train_sideinfo_fusion(tar, side_tar, non, side_non, lambda, prior);
end
