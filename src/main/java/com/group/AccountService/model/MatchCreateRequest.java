package com.group.AccountService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.group.AccountService.model.Match;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchCreateRequest {

    private String userId;
    private Match match;

}
