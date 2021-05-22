CoG_naive = sum(distGC_vect)/len(distGC_vect)
CoG_volumetric = sum(1 / i for i in distGC_vect) /\
        sum(1 / i**2 for i in distGC_vect)
