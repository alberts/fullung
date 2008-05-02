function [lr_scores]=lr_fusion(w,scores)
llr_scores = lin_fusion(w,scores);
% clip LLRs to +/- some threshold
thresh = 15;
clipped_llr_scores = clip(llr_scores, thresh);
% convert LLRs to LRs
lr_scores = exp(clipped_llr_scores);
