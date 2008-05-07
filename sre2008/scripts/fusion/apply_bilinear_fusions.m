function llr = apply_bilinear_fusions(W,scores_sideinfo,systems,sidestages)
llr = scores_sideinfo(systems,:);
for i=1:1:length(W)
    sideinfo = scores_sideinfo(sidestages{i},:);
    llr = apply_bilinear_fusion(W{i},llr,sideinfo);
end
