package com.example.rental;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        combinationSumreq(0, 0, candidates, new ArrayList<>(), target, result);

        return result;
    }

    public void combinationSumreq(int currentSum, int startIndex, int[] candidates,
                                  List<Integer> currentCombination, int target,
                                  List<List<Integer>> result) {
        if (currentSum == target) {
            List<Integer> cc=(new ArrayList<>(currentCombination).stream().sorted().collect(Collectors.toList()));
            if(!(result.contains(cc))){
                result.add(cc);
            }
            return;
        }

        if (currentSum > target) {
            return;
        }

        for (int i = startIndex+1; i < candidates.length; i++) {
            currentCombination.add(candidates[i]);
            combinationSumreq(currentSum + candidates[i], i, candidates, currentCombination, target, result);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
}



class main {
    public static void main(String[] args) {
        Solution solution=new Solution();
        int[] integers=new int[]{2,3,6,7};
        solution.combinationSum(integers,7);

    }
}