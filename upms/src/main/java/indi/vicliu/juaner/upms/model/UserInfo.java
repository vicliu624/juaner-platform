/**
 * @Auther: vicliu
 * Date: 2021/2/15 下午2:14
 * @Description:
 */

package indi.vicliu.juaner.upms.model;

import apijson.MethodAccess;
import indi.vicliu.juaner.jsonapi.JSONAPITable;

import static apijson.RequestRole.*;
import static apijson.RequestRole.ADMIN;


@JSONAPITable(realTableName = "tbl_user_info")
@MethodAccess(
        GET = {},
        GETS = {OWNER, ADMIN},
        POST = {UNKNOWN, ADMIN},
        DELETE = {ADMIN}
)
public class UserInfo {
}
