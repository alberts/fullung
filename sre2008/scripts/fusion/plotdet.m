function [DCF_opt, eer] = plotdet(true_scores, false_scores, colorcode)

Cmiss = 10;
Cfa = 1;
Ptarget = 0.01;
Set_DCF(Cmiss, Cfa, Ptarget);
[P_miss,P_fa] = Compute_DET(true_scores, false_scores);

Pmiss_min = 0.0005+eps;
Pmiss_max = 0.50;
Pfa_min = 0.0005+eps;
Pfa_max = 0.50;
Set_DET_limits(Pmiss_min,Pmiss_max,Pfa_min,Pfa_max);

hold on;
Plot_DET(P_miss, P_fa,colorcode);
title('Speaker Detection Performance');

%find lowest cost point and plot
C_miss = 10;
C_fa = 1;
P_target = 0.01;
Set_DCF(C_miss,C_fa,P_target);
[DCF_opt Popt_miss Popt_fa] = Min_DCF(P_miss,P_fa);
Plot_DET (Popt_miss,Popt_fa,'ko');
hold off;

%nfa = 898;
%Pact_fa = nfa / length(false_scores);
%nmiss = 228;
%Pact_miss = nmiss / length(true_scores);
%[DCF_act Pact_miss Pact_fa] = Min_DCF(Pact_miss, Pact_fa);
%hold on; Plot_DET(Pact_miss, Pact_fa,'ks'); hold off;

C_default = min([C_miss*P_target C_fa*(1-P_target)]);
C_norm = DCF_opt / C_default;

eer = Equal_Error_Rate(P_miss, P_fa);

disp(sprintf('EER = %.16f', eer));
disp(sprintf('Minimum DCF = %.16f', DCF_opt));
