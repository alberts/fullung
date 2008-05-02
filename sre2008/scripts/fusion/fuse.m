function fuse

prior = effective_prior(0.01,10,1);

train_tars(1,:) = load('eval05_malesys_targ.txt');
train_nontars(1,:) = load('eval05_malesys_non.txt');
train_tars(2,:) = load('eval05_femalesys_targ.txt');
train_nontars(2,:) = load('eval05_femalesys_non.txt');
train_tars(3,:) = load('eval05_both_targ.txt');
train_nontars(3,:) = load('eval05_both_non.txt');

w = train_llr_fusion(train_tars,train_nontars,prior);
w0 = train_llr_fusion(train_tars(3,:),train_nontars(3,:),prior);

tars(1,:) = load('eval06_malesys_targ.txt');
non_tars(1,:) = load('eval06_malesys_non.txt');
tars(2,:) = load('eval06_femalesys_targ.txt');
non_tars(2,:) = load('eval06_femalesys_non.txt');
tars(3,:) = load('eval06_both_targ.txt');
non_tars(3,:) = load('eval06_both_non.txt');

[fused_tar_lrs,fused_nontar_lrs]=lr_fusion(w,tars,non_tars);
[both_tar_lrs,both_nontar_lrs]=lr_fusion(w0,tars(3,:),non_tars(3,:));

figure;
hold on;
colors = 'rgb';
for i=1:1:size(tars, 1)
    plotdet(tars(i,:), non_tars(i,:), colors(i));
end
plotdet(fused_tar_lrs, fused_nontar_lrs, 'k');
hold off;

figure;
both_tar_llrs = log(both_tar_lrs);
both_nontar_llrs = log(both_nontar_lrs);
fused_tar_llrs = log(fused_tar_lrs);
fused_nontar_llrs = log(fused_nontar_lrs);
ape_plot({'both',{both_tar_llrs,both_nontar_llrs}},{'fusion',{fused_tar_llrs,fused_nontar_llrs}});

function [fused_targets,fused_non_targets]=lr_fusion(w,targets,non_targets)
fused_targets = lin_fusion(w,targets);
fused_non_targets = lin_fusion(w,non_targets);

% clip LLRs to +/- some threshold
thresh = 15;
fused_targets = clip(fused_targets, thresh);
fused_non_targets = clip(fused_non_targets, thresh);

% convert LLRs to LRs
fused_targets = exp(fused_targets);
fused_non_targets = exp(fused_non_targets);

function [x] = clip(x, thresh)
thresh=abs(thresh);
x(x>thresh)=thresh;
x(x<-thresh)=-thresh;
