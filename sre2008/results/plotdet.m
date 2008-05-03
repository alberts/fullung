function plotdet(evalfile,plot_code)

disp(evalfile);

fid = fopen(evalfile);
C = textscan(fid,'%s%s%s%s%s%s%s%s%f%s%s%s');
C
fclose(fid);

scores = C{9};

true_speaker_scores = scores(strcmp(C{10},'targ')==1|strcmp(C{10},'target')==1);
impostor_scores = scores(strcmp(C{10},'non')==1|strcmp(C{10},'nontarget')==1);

size(true_speaker_scores)
size(impostor_scores)

Cmiss = 10;
Cfa = 1;
Ptarget = 0.01;
Set_DCF(Cmiss, Cfa, Ptarget);

Pmiss_min = 0.0005+eps;
Pmiss_max = 0.50;
Pfa_min = 0.0005+eps;
Pfa_max = 0.50;
Set_DET_limits(Pmiss_min,Pmiss_max,Pfa_min,Pfa_max);

[P_miss,P_fa] = Compute_DET (true_speaker_scores, impostor_scores);

%plot DET-curve
Plot_DET (P_miss, P_fa,plot_code);
title ('Speaker Detection Performance');
hold on;

%find lowest cost point and plot
Set_DCF(Cmiss,Cfa,Ptarget);
[DCF_opt Popt_miss Popt_fa] = Min_DCF(P_miss,P_fa);
Plot_DET (Popt_miss,Popt_fa,'ko');

eer = Equal_Error_Rate(P_miss, P_fa);

disp(sprintf('EER = %.16f', eer));
disp(sprintf('Minimum DCF = %.16f', DCF_opt));
