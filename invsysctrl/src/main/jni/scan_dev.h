#define SCANDEV_MAJOR   	241	

#define MAGIC_GPIO		's'

/*

io index definition 
#define GPIOA(n)        (PA_NR_BASE + (n))
#define GPIOB(n)        (PB_NR_BASE + (n))
#define GPIOC(n)        (PC_NR_BASE + (n))
#define GPIOD(n)        (PD_NR_BASE + (n))
#define GPIOE(n)        (PE_NR_BASE + (n))
#define GPIOF(n)        (PF_NR_BASE + (n))
#define GPIOG(n)        (PG_NR_BASE + (n))
#define GPIOH(n)        (PH_NR_BASE + (n))
#define GPIOI(n)        (PI_NR_BASE + (n))

port number for each pio 
#define PA_NR           18
#define PB_NR           24
#define PC_NR           25
#define PD_NR           28
#define PE_NR           12
#define PF_NR           6
#define PG_NR           12
#define PH_NR           28
#define PI_NR           22

*/

#define IOCTL_GPIOA_OUT_SET     _IOW(MAGIC_GPIO,10,int)
#define IOCTL_GPIOA_OUT_CLR     _IOW(MAGIC_GPIO,11,int)

#define IOCTL_GPIOB_OUT_SET     _IOW(MAGIC_GPIO,12,int)
#define IOCTL_GPIOB_OUT_CLR     _IOW(MAGIC_GPIO,13,int)

#define IOCTL_GPIOC_OUT_SET      _IOW(MAGIC_GPIO,14,int)
#define IOCTL_GPIOC_OUT_CLR     _IOW(MAGIC_GPIO,15,int)

#define IOCTL_GPIOD_OUT_SET     _IOW(MAGIC_GPIO,16,int)
#define IOCTL_GPIOD_OUT_CLR     _IOW(MAGIC_GPIO,17,int)

#define IOCTL_GPIOE_OUT_SET     _IOW(MAGIC_GPIO,18,int)
#define IOCTL_GPIOE_OUT_CLR     _IOW(MAGIC_GPIO,19,int)

#define IOCTL_GPIOF_OUT_SET     _IOW(MAGIC_GPIO,20,int)
#define IOCTL_GPIOF_OUT_CLR     _IOW(MAGIC_GPIO,21,int)

#define IOCTL_GPIOG_OUT_SET     _IOW(MAGIC_GPIO,22,int)
#define IOCTL_GPIOG_OUT_CLR     _IOW(MAGIC_GPIO,23,int)

#define IOCTL_GPIOH_OUT_SET     _IOW(MAGIC_GPIO,24,int)
#define IOCTL_GPIOH_OUT_CLR     _IOW(MAGIC_GPIO,25,int)

#define IOCTL_GPIOI_OUT_SET     _IOW(MAGIC_GPIO,26,int)
#define IOCTL_GPIOI_OUT_CLR     _IOW(MAGIC_GPIO,27,int)

#define IOCTL_GPA0_OUT_SET     _IOW(MAGIC_GPIO,10,int)
#define IOCTL_GPA0_OUT_CLR     _IOW(MAGIC_GPIO,11,int)
#define IOCTL_GPA1_OUT_SET     _IOW(MAGIC_GPIO,12,int)
#define IOCTL_GPA1_OUT_CLR     _IOW(MAGIC_GPIO,13,int)

#define IOCTL_GPB_OUT_SET      _IOW(MAGIC_GPIO,14,int)
#define IOCTL_GPB_OUT_CLR      _IOW(MAGIC_GPIO,60,int)

#define IOCTL_GPC0_OUT_SET     _IOW(MAGIC_GPIO,15,int)
#define IOCTL_GPC0_OUT_CLR     _IOW(MAGIC_GPIO,16,int)
#define IOCTL_GPC1_OUT_SET     _IOW(MAGIC_GPIO,17,int)
#define IOCTL_GPC1_OUT_CLR     _IOW(MAGIC_GPIO,18,int)

#define IOCTL_GPD0_OUT_SET     _IOW(MAGIC_GPIO,19,int)
#define IOCTL_GPD0_OUT_CLR     _IOW(MAGIC_GPIO,20,int)
#define IOCTL_GPD1_OUT_SET     _IOW(MAGIC_GPIO,21,int)
#define IOCTL_GPD1_OUT_CLR     _IOW(MAGIC_GPIO,22,int)

#define IOCTL_GPE0_OUT_SET     _IOW(MAGIC_GPIO,23,int)
#define IOCTL_GPE0_OUT_CLR     _IOW(MAGIC_GPIO,24,int)
#define IOCTL_GPE1_OUT_SET     _IOW(MAGIC_GPIO,25,int)
#define IOCTL_GPE1_OUT_CLR     _IOW(MAGIC_GPIO,26,int)

#define IOCTL_GPF0_OUT_SET     _IOW(MAGIC_GPIO,27,int)
#define IOCTL_GPF0_OUT_CLR     _IOW(MAGIC_GPIO,28,int)
#define IOCTL_GPF1_OUT_SET     _IOW(MAGIC_GPIO,29,int)
#define IOCTL_GPF1_OUT_CLR     _IOW(MAGIC_GPIO,30,int)
#define IOCTL_GPF2_OUT_SET     _IOW(MAGIC_GPIO,31,int)
#define IOCTL_GPF2_OUT_CLR     _IOW(MAGIC_GPIO,32,int)
#define IOCTL_GPF3_OUT_SET     _IOW(MAGIC_GPIO,33,int)
#define IOCTL_GPF3_OUT_CLR     _IOW(MAGIC_GPIO,34,int)

#define IOCTL_GPG0_OUT_SET     _IOW(MAGIC_GPIO,35,int)
#define IOCTL_GPG0_OUT_CLR     _IOW(MAGIC_GPIO,36,int)
#define IOCTL_GPG1_OUT_SET     _IOW(MAGIC_GPIO,37,int)
#define IOCTL_GPG1_OUT_CLR     _IOW(MAGIC_GPIO,38,int)
#define IOCTL_GPG2_OUT_SET     _IOW(MAGIC_GPIO,39,int)
#define IOCTL_GPG2_OUT_CLR     _IOW(MAGIC_GPIO,40,int)
#define IOCTL_GPG3_OUT_SET     _IOW(MAGIC_GPIO,41,int)
#define IOCTL_GPG3_OUT_CLR     _IOW(MAGIC_GPIO,42,int)

#define IOCTL_GPH0_OUT_SET     _IOW(MAGIC_GPIO,43,int)
#define IOCTL_GPH0_OUT_CLR     _IOW(MAGIC_GPIO,44,int)
#define IOCTL_GPH1_OUT_SET     _IOW(MAGIC_GPIO,45,int)
#define IOCTL_GPH1_OUT_CLR     _IOW(MAGIC_GPIO,46,int)
#define IOCTL_GPH2_OUT_SET     _IOW(MAGIC_GPIO,47,int)
#define IOCTL_GPH2_OUT_CLR     _IOW(MAGIC_GPIO,48,int)
#define IOCTL_GPH3_OUT_SET     _IOW(MAGIC_GPIO,49,int)
#define IOCTL_GPH3_OUT_CLR     _IOW(MAGIC_GPIO,50,int)

#define IOCTL_GPI_OUT_SET      _IOW(MAGIC_GPIO,51,int)
#define IOCTL_GPI_OUT_CLR      _IOW(MAGIC_GPIO,61,int)

#define IOCTL_GPJ0_OUT_SET     _IOW(MAGIC_GPIO,52,int)
#define IOCTL_GPJ0_OUT_CLR     _IOW(MAGIC_GPIO,53,int)
#define IOCTL_GPJ1_OUT_SET     _IOW(MAGIC_GPIO,54,int)
#define IOCTL_GPJ1_OUT_CLR     _IOW(MAGIC_GPIO,55,int)
#define IOCTL_GPJ2_OUT_SET     _IOW(MAGIC_GPIO,56,int)
#define IOCTL_GPJ2_OUT_CLR     _IOW(MAGIC_GPIO,57,int)
#define IOCTL_GPJ3_OUT_SET     _IOW(MAGIC_GPIO,58,int)
#define IOCTL_GPJ3_OUT_CLR     _IOW(MAGIC_GPIO,59,int)

#define IOCTL_GET_RESUME	_IOW(MAGIC_GPIO,100,int)
#define IOCTL_SET_RESUME	_IOW(MAGIC_GPIO,101,int)
#define IOCTL_SPKR_INV		_IOW(MAGIC_GPIO,102,int)
#define IOCTL_SPKR_NOR		_IOW(MAGIC_GPIO,103,int)
