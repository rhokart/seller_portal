package com.rhokart.sellerportal.repositories;

import com.rhokart.sellerportal.models.CategoryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryRepository {

    @Select("SELECT catg_id, catg_l1, catg_l2, catg_l3, is_actv " +
            "FROM product.catg_dtl;")
    @Results({
            @Result(property = "catgId", column = "catg_id"),
            @Result(property = "catgL1", column = "catg_l1"),
            @Result(property = "catgL2", column = "catg_l2"),
            @Result(property = "catgL3", column = "catg_l3"),
            @Result(property = "isActv", column = "is_actv")
    })
    List<CategoryDetail> getAllCategoryDetails();
}
