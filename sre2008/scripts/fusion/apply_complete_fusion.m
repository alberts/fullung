function llr = apply_complete_fusion(sysweights,w,scores,calibrate_sideinfos,fuse_sideinfo)
llr = scores;
% apply with calibration weights
for i=1:1:length(sysweights)
    llr(i,:) = apply_bilinear_fusions(sysweights{i}, llr(i,:), calibrate_sideinfos);
end
% apply with fusion weight
llr = apply_bilinear_fusion(w, llr, fuse_sideinfo);
