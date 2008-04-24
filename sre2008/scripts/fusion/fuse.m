function fuse

train_targets(1,:) = load('eval05_malesys_targ.txt');
train_non_targets(1,:) = load('eval05_malesys_non.txt');
train_targets(2,:) = load('eval05_femalesys_targ.txt');
train_non_targets(2,:) = load('eval05_femalesys_non.txt');

prior = effective_prior(0.01,10,1);
w = train_llr_fusion(train_targets,train_non_targets,prior);

w

targets(1,:) = load('eval05_malesys_targ.txt');
non_targets(1,:) = load('eval05_malesys_non.txt');
targets(2,:) = load('eval05_femalesys_targ.txt');
non_targets(2,:) = load('eval05_femalesys_non.txt');

[fused_targets,fused_non_targets]=llr_fusion(w,targets,non_targets);

figure;
hold on;
for i=1:1:size(targets, 1)
    targ = targets(i,:);
    non = non_targets(i,:);
    plotdet(targ, non, 'b');
end
plotdet(fused_targets, fused_non_targets, 'r');
hold off;

function [fused_targets,fused_non_targets]=llr_fusion(w,targets,non_targets)
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
