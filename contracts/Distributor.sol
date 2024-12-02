pragma solidity ^0.4.25;
// 引用角色库
import "./Roles.sol";

//分销商角色
contract Distributor {
    // 导入库应用于Role
    using Roles for Roles.Role;
    // 定义分销商增加和删除的事件
    event DistributorAdded(address indexed account);
    event DistributorRemoved(address indexed account);
    // 分销商变量
    Roles.Role private _distributors;
    
    // 构造器初始化时添加分销商
    constructor(address distributor) public {
        _addDistributor(distributor);
    }
    
    // 函数执行前判断函数调用者是否是分销商
    modifier onlyDistributor() {
        require(isDistributor(msg.sender),"DistributorRole: caller does not have the Distributor role");
        _;
    }
    
    // 判断一个地址是否是分销商
    function isDistributor(address account) public view returns (bool) {
        return _distributors.has(account);
    }
    
    // 增加分销商的方法
    function addDistributor(address account) public onlyDistributor {
        _addDistributor(account);
    }
    
    // 移除分销商的方法
    function renounceDistributor() public {
        _removeDistributor(msg.sender);
    }
    
    // 增加分销商的方法
    function _addDistributor(address account) internal {
        _distributors.add(account);
        emit DistributorAdded(account);
    }
    
    //移除分销商的方法
    function _removeDistributor(address account) internal {
        _distributors.remove(account);
        emit DistributorRemoved(account);
    }
}