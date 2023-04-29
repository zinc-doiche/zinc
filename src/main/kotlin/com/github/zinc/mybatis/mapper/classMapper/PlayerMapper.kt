package com.github.zinc.mybatis.mapper.classMapper

import com.github.zinc.player.domain.PlayerVO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PlayerMapper {
    fun insert(playerName: String)
    fun select(playerName: String): PlayerVO?
}