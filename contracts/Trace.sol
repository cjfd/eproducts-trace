pragma solidity >=0.4.22 <0.7.0;
pragma experimental ABIEncoderV2;
import "./EproductsInfoItem.sol";
import "./Distributor.sol";
import "./Producer.sol";
import "./Retailer.sol";

contract Trace is Producer, Distributor, Retailer {
    mapping(uint256 => address) eproducts;
    uint[] eproductsList;
    
    constructor(
        address producer,
        address distributor,
        address retailer
    ) public
        Producer(producer)
        Distributor(distributor)
        Retailer(retailer) {}
        
    function newEproducts(
        string name,
        uint256 traceNumber,
        string traceName,
        uint8 quality
    ) public onlyProducer returns (address) {
        require(eproducts[traceNumber] == address(0),"traceNumber already exist");
        EproductsInfoItem eproduct = new EproductsInfoItem(name,traceName,quality,msg.sender);
        eproducts[traceNumber] = eproduct;
        eproductsList.push(traceNumber);
        return eproduct;
    }
    
    function addTraceInfoByDistributor(
    uint256 traceNumber,
    string traceName,
    uint8 quality
    ) public onlyDistributor returns (bool) {
        require(eproducts[traceNumber] != address(0),"traceNumber does not exist");
        return EproductsInfoItem(eproducts[traceNumber]).addTraceInfoByDistributor(traceName,msg.sender,quality);
    }
    
    function addTraceInfoByRetailer(
    uint256 traceNumber,
    string traceName,
    uint8 quality
    ) public onlyRetailer returns (bool) {
        require(eproducts[traceNumber] != address(0),"traceNumber does not exist");
        return EproductsInfoItem(eproducts[traceNumber]).addTraceInfoByRetailer(traceName,msg.sender,quality);
    }
    
    function getTraceInfo(uint256 traceNumber) public constant returns (uint[], string[], address[], uint8[]) {
        require(eproducts[traceNumber] != address(0),"traceNumber does not exist");
        return EproductsInfoItem(eproducts[traceNumber]).getTraceInfo();
    }
    
    function getEproduct(uint256 traceNumber) public constant returns (uint, string, string, string, address, uint8) {
        require(eproducts[traceNumber] != address(0),"traceNumber does not exist");
        return EproductsInfoItem(eproducts[traceNumber]).getEproducts();
    }
    
    function getAllEproducts() public constant returns (uint[]) {
        return eproductsList;
    }
}