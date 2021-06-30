#include <stdio.h>
#include <stddef.h>
#include <memory.h>

#include "/usr/lib/jvm/graalvm/lib/svm/clibraries/linux-amd64/include/aarch64cpufeatures.h"

int AArch64LibCHelperDirectives() {
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(CPUFeatures)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fA53MAC:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fA53MAC)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fA53MAC:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fA53MAC)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fA53MAC = all_bits_set;
        is_unsigned = fieldHolder.fA53MAC > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fA53MAC:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fAES:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fAES)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fAES:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fAES)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fAES = all_bits_set;
        is_unsigned = fieldHolder.fAES > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fAES:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fASIMD:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fASIMD)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fASIMD:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fASIMD)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fASIMD = all_bits_set;
        is_unsigned = fieldHolder.fASIMD > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fASIMD:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fCRC32:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fCRC32)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fCRC32:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fCRC32)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fCRC32 = all_bits_set;
        is_unsigned = fieldHolder.fCRC32 > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fCRC32:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fDMBATOMICS:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fDMBATOMICS)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fDMBATOMICS:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fDMBATOMICS)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fDMBATOMICS = all_bits_set;
        is_unsigned = fieldHolder.fDMBATOMICS > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fDMBATOMICS:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fEVTSTRM:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fEVTSTRM)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fEVTSTRM:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fEVTSTRM)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fEVTSTRM = all_bits_set;
        is_unsigned = fieldHolder.fEVTSTRM > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fEVTSTRM:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fFP:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fFP)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fFP:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fFP)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fFP = all_bits_set;
        is_unsigned = fieldHolder.fFP > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fFP:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fLSE:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fLSE)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fLSE:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fLSE)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fLSE = all_bits_set;
        is_unsigned = fieldHolder.fLSE > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fLSE:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fPMULL:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fPMULL)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fPMULL:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fPMULL)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fPMULL = all_bits_set;
        is_unsigned = fieldHolder.fPMULL > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fPMULL:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA1:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fSHA1)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA1:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fSHA1)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fSHA1 = all_bits_set;
        is_unsigned = fieldHolder.fSHA1 > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA1:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA2:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fSHA2)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA2:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fSHA2)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fSHA2 = all_bits_set;
        is_unsigned = fieldHolder.fSHA2 > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSHA2:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSTXRPREFETCH:PropertyInfo:size=%lu\n", ((unsigned long)sizeof(((CPUFeatures *) 0)->fSTXRPREFETCH)));
    printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSTXRPREFETCH:PropertyInfo:offset=%lu\n", ((unsigned long)offsetof(CPUFeatures, fSTXRPREFETCH)));
    {
        int is_unsigned;
        unsigned long all_bits_set = -1;
        CPUFeatures fieldHolder;
        memset(&fieldHolder, 0x0, sizeof(fieldHolder));
        fieldHolder.fSTXRPREFETCH = all_bits_set;
        is_unsigned = fieldHolder.fSTXRPREFETCH > 0;
        printf("NativeCodeInfo:AArch64LibCHelperDirectives:StructInfo:CPUFeatures:StructFieldInfo:fSTXRPREFETCH:PropertyInfo:signedness=$%s$\n", (is_unsigned) ? "UNSIGNED" : "SIGNED");
    }
    return 0;
}

int main(void) {
    return AArch64LibCHelperDirectives();
}
