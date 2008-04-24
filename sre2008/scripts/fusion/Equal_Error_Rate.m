function [EER] = Equal_Error_Rate(Pmiss, Pfa)
dZero = find((Pfa - Pmiss) == 0);
if dZero > 0
    EER = min(Pmiss(dZero));
    return;
else
    dLess = find((Pfa - Pmiss) < 0);
    dLess = dLess(1);
    EER = (Pfa(dLess - 1)+Pfa(dLess))/2;
end;
