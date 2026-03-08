const hre = require("hardhat");

async function main() {
    console.log("Deploying Voting contract...");

    const Voting = await hre.ethers.getContractFactory("Voting");
    const voting = await Voting.deploy();

    await voting.waitForDeployment();

    const contractAddress = await voting.getAddress();
    console.log(`✅ Voting contract deployed to: ${contractAddress}`);
    console.log("");
    console.log("=== IMPORTANT ===");
    console.log("Copy the contract address above and paste it into:");
    console.log("src/main/resources/application.properties");
    console.log("as the value of: blockchain.contract-address");
    console.log("=================");
    console.log("");
    console.log("Contract ABI is available at:");
    console.log("blockchain/artifacts/contracts/Voting.sol/Voting.json");
}

main().catch((error) => {
    console.error(error);
    process.exitCode = 1;
});
