#!/usr/bin/env python

import xarray as xr
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt 
import pathlib as P
import argparse


def main():
    '''Plot differences between 2 tests
    test1: path to the first wrfout file
    test2: path to the second wrfout file'''

    parser = argparse.ArgumentParser()
    parser.add_argument("test1")
    parser.add_argument("test2")
    args = parser.parse_args()

    path1=P.Path(args.test1)
    path2=P.Path(args.test2)

    case1 = xr.open_dataset(path1/"wrfout_d01_2000-01-24_12:00:00")
    case2 = xr.open_dataset(path2/"wrfout_d01_2000-01-24_12:00:00")

    # Time coordinate
    case1 = case1.assign_coords({'Time':case1.XTIME})
    case2 = case2.assign_coords({'Time':case2.XTIME})


    # Open figure
    T2_1 = case1["T2"].sel(Time='2000-01-25')
    T2_1 = T2_1.assign_coords({'case':'1st case'})
    T2_2 = case2["T2"].sel(Time='2000-01-25')
    T2_2 = T2_2.assign_coords({'case':'2nd case'})

    diff = case1["T2"] - case2["T2"]
    diff = diff.sel(Time='2000-01-25')
    diff = diff.assign_coords({'case':'Abs. diff'})

    toplot = xr.concat([T2_1, T2_2, diff],'case')
    
    fig,ax = plt.subplots(2,2)
    toplot.sel(case='1st case').plot(ax=ax[0,0])
    ax[0,0].set_title('1st case')
    toplot.sel(case='2nd case').plot(ax=ax[0,1])
    ax[0,1].set_title('2nd case')
    toplot.sel(case='Abs. diff').plot(ax=ax[1,0])
    ax[1,0].set_title('Abs. diff')
    fig.savefig("diff.png")

if __name__ == "__main__":
    main()

