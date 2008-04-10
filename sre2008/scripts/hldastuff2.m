function hldastuff2
clear java
javaaddpath({'C:\home\albert\work8\lre\bin'});
eval('import za.ac.sun.sdv_sun_0.HLDADriver');

driver = HLDADriver;

driver.calculate();
SIGMA_g = driver.getGlobalCov();
SIGMA = cell(driver.getClassCovs());
gamma = driver.getGamma();

A = eye(size(SIGMA_g));
% keep all the dimensions
p = 24;
iters = 200;

disp('estimating HLDA transform')
B = hlda_optimizer(A, p, SIGMA_g, SIGMA, gamma, iters);
% fix transformation matrix
Trans = B(:,1:p)';
size(Trans)
driver.writeMatrix('C:\LRE2008\work\sdv_sun_0\hlda.dat', Trans);
